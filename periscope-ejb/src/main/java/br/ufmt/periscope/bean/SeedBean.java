package br.ufmt.periscope.bean;

import br.ufmt.periscope.model.ApplicantType;
import java.io.InputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.faces.bean.ApplicationScoped;
import javax.inject.Inject;

import org.bson.types.Code;

import br.ufmt.periscope.model.Country;
import br.ufmt.periscope.model.User;

import com.bigfatgun.fixjures.Fixjure;
import com.bigfatgun.fixjures.yaml.YamlSource;
import com.github.jmkgreen.morphia.Datastore;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;

@ApplicationScoped
@Singleton
@Startup
public class SeedBean {

	private @Inject Datastore ds;
	private @Inject Logger log;	

	@PostConstruct
	public void atStartup() {
		log.info("Inicializando seeder");
		initUsers();
		initCountries();
		initApplicantTypes();
		insertAlgorithFromFile("lcs","js/longestCommonSubstring.js");
		insertAlgorithFromFile("levenshtein","js/levenshtein.js");
		insertAlgorithFromFile("LiquidMetal","js/liquidmetal.js");
	}

	private void insertAlgorithFromFile(String name,String path) {
		
		DB db = ds.getDB();
		
		DBCollection functionsCollection = db.getCollectionFromString("system.js");			
		InputStream is = SeedBean.class.getClassLoader().getResourceAsStream(path);
        String function = new Scanner(is,"UTF-8").useDelimiter("\\A").next();
			
		if(function == null){
			log.log(Level.SEVERE,"Erro ao ler o arquivo com a função "+name);
		}
		Code code = new Code(function);
		BasicDBObject newFunction = new BasicDBObject();
		newFunction.put("_id", name);
		newFunction.put("value", code);				
		
		functionsCollection.save(newFunction);
		
	}

	private void initApplicantTypes() {
            if (ds.getCount(ApplicantType.class) == 0l) {
                log.info("Nenhuma Natureza encontrada.");
                List<ApplicantType> applicantTypes = Fixjure
                        .listOf(ApplicantType.class)
                        .from(YamlSource
                        .newYamlResource("applicantType-inicial.yaml"))
                        .create();
                Iterator<ApplicantType> it = applicantTypes.iterator();
                while(it.hasNext()){
                    ds.save(it.next());
                }
                log.info("Cadastrado "+applicantTypes.size()+" Naturezas.");
            }

	}

	private void initCountries() {
		if (ds.getCount(Country.class) == 0l) {
			log.info("Nenhum país encontrado.");
			List<Country> countries = Fixjure
					.listOf(Country.class)
					.from(YamlSource
							.newYamlResource("country-inicial-data.yaml"))
					.create();
			Iterator<Country> it = countries.iterator();
			while (it.hasNext()) {
				ds.save(it.next());
			}
			log.info("Cadastrado " + countries.size() + " países.");
		}
	}

	private void initUsers() {
		if (ds.getCount(User.class) == 0l) {
			log.info("Nenhum usuário encontrado.");
			List<User> users = Fixjure.listOf(User.class)
					.from(YamlSource.newYamlResource("user-inicial.yaml"))
					.create();
			Iterator<User> it = users.iterator();
			while (it.hasNext()) {
				ds.save(it.next());
			}
			log.info("Cadastrado " + users.size() + " usuários.");
		}
	}
}
