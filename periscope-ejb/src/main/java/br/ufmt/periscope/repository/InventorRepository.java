package br.ufmt.periscope.repository;

import br.ufmt.periscope.model.Inventor;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import br.ufmt.periscope.model.Patent;
import br.ufmt.periscope.model.Project;
import br.ufmt.periscope.report.Pair;
import br.ufmt.periscope.util.Filters;

import com.github.jmkgreen.morphia.Datastore;
import com.github.jmkgreen.morphia.mapping.Mapper;
import com.github.jmkgreen.morphia.mapping.cache.EntityCache;
import com.google.common.collect.HashMultiset;
import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopScoreDocCollector;
import org.apache.lucene.util.Version;

@Named
public class InventorRepository {

	private @Inject
	Datastore ds;
	private @Inject IndexReader reader;
	private @Inject StandardAnalyzer analyzer;

	public List<Pair> updateInventors(Project currentProject,int limit, Filters filtro) {

        /**
         * db.Patent.aggregate( {$match:{"project.$id":new
         * ObjectId("51db042d44ae70d2d3649c20")}}, {$match:{blacklisted:false}},
         * {$unwind:"$inventors"},
         * {$group:{_id:"$inventors",applicationPerInventor:{$sum:1}}},
         * {$sort:{applicationPerInventor:-1}}, { $limit : 5 } );
         */
        ArrayList<DBObject> parametros = new ArrayList<DBObject>();

        DBObject matchProj = new BasicDBObject();
        matchProj.put("$match",
                new BasicDBObject("project.$id", currentProject.getId()));

        if (filtro.isComplete()) {
            DBObject matchComplete = new BasicDBObject();
            matchComplete.put("$match", new BasicDBObject("completed", filtro.isComplete()));
            parametros.add(matchComplete);
        }

        DBObject matchDate = new BasicDBObject();
        if (filtro.getSelecionaData() == 1) {
            matchDate.put("$match", new BasicDBObject("publicationDate", new BasicDBObject("$gte", filtro.getInicio()).append("$lte", filtro.getFim())));
        } else {
            matchDate.put("$match", new BasicDBObject("applicationDate", new BasicDBObject("$gte", filtro.getInicio()).append("$lte", filtro.getFim())));
        }
        parametros.add(matchDate);

        DBObject matchBlacklist = new BasicDBObject();
        matchBlacklist.put("$match", new BasicDBObject("blacklisted", false));
        parametros.add(matchBlacklist);

        DBObject unwind = new BasicDBObject("$unwind", "$inventors");
        parametros.add(unwind);

        DBObject group = new BasicDBObject();
        parametros.add(group);
        DBObject fields = new BasicDBObject("_id", "$inventors");
        fields.put("applicationPerInventor", new BasicDBObject("$sum", 1));
        group.put("$group", fields);

        DBObject sort = new BasicDBObject("$sort", new BasicDBObject(
                "applicationPerInventor", -1));
        parametros.add(sort);

        System.out.println(limit);
        DBObject pipeLimit = new BasicDBObject("$limit", limit);
        parametros.add(pipeLimit);

        DBObject[] parameters = new DBObject[parametros.size()];
        parameters = parametros.toArray(parameters);

        AggregationOutput output = ds.getCollection(Patent.class).aggregate(matchProj, parameters);

        BasicDBList outputResult = (BasicDBList) output.getCommandResult().get(
                "result");

        List<Pair> pairs = new ArrayList<Pair>();
        for (Object object : outputResult) {
            DBObject aux = (DBObject) object;
            DBObject inventorName = (DBObject) aux.get("_id");
            String inventor = inventorName.get("name").toString();
            Integer count = (Integer) aux.get("applicationPerInventor");

            pairs.add(new Pair(inventor, count));
        }
        return pairs;
	}
        
	public ArrayList<Inventor> getInventors(Project currentProject) {
		Map<String,Inventor> map = new HashMap<String, Inventor>();
		
		HashMultiset<String> bag = HashMultiset.create(); 
		
		BasicDBObject where = new BasicDBObject();		
		where.put("project.$id", currentProject.getId());		
		where.put("inventors", new BasicDBObject("$exists", true));
		
		BasicDBObject keys = new BasicDBObject();
		keys.put("inventors",1);
								
		DBCursor cursor = ds.getCollection(Patent.class).find(where, keys);
		Mapper mapper = ds.getMapper();
		EntityCache ec = mapper.createEntityCache();
		while(cursor.hasNext()){
			
			BasicDBList objList = (BasicDBList) cursor.next().get("inventors");			
			Iterator<Object> itList = objList.iterator();
			while(itList.hasNext()){
				Inventor pa = (Inventor) mapper.fromDBObject(Inventor.class,(DBObject) itList.next(), ec);
				bag.add(pa.getName());
				pa.setDocumentCount(bag.count(pa.getName()));
				map.put(pa.getName(),pa);
			}								
						
		}
		return new ArrayList<Inventor>(map.values());
	}
        
	public Set<String> getInventorSugestions(Project project,int top,String... names){
		
	    Set<String> results = new HashSet<String>();							
		try {
			StringBuilder queryBuilder = new StringBuilder();			
			for(String name : names){
				String[] terms = name.split(" ",-2);
				for(String term : terms){
					//if(term.length() >= 4){		
						queryBuilder.append(term+"~ ");
						queryBuilder.append(term+"* ");
					//}
				}	
				
				//queryBuilder.append("NOT \""+name+"\" ");	
				queryBuilder.append("\""+name+"\"~10 ");
			}		
						
			TopScoreDocCollector collector = TopScoreDocCollector.create(1000, true);			
			BooleanQuery bq = new BooleanQuery();			
			Query queryPa = new QueryParser(Version.LUCENE_36, "inventor", analyzer)
											.parse(queryBuilder.toString());
			queryPa.setBoost(10f);		
			
			Query queryProject = new QueryParser(Version.LUCENE_36, "project", analyzer)
											.parse(project.getId().toString());
			queryProject.setBoost(0.1f);					
			
			bq.add(queryPa, BooleanClause.Occur.MUST);
			bq.add(queryProject,BooleanClause.Occur.MUST);
			System.out.println(bq);
			
			IndexSearcher searcher = new IndexSearcher(reader);
			searcher.search(bq, collector);							
			
			ScoreDoc[] hits = collector.topDocs().scoreDocs;
		    System.out.println("Found " + hits.length + " hits.");
		    for(int i=0;i<hits.length;++i) {	    			    	
		      int docId = hits[i].doc;
		      Document d = searcher.doc(docId);	      		      
		      //System.out.println((i + 1) + ". " + d.get("applicant") + "\t" + hits[i].score );		      
		      results.add(d.get("inventor"));
		      
		      if(results.size() == top ) break;
		    }
		    for(String name : names){
		    	results.remove(name);
		    }
		    searcher.close();
		    
		    return results;
		    		
		} catch (CorruptIndexException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return results;		
				
		
	}
}
