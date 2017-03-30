package fr.cea.organicity.manager.config;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.SQLException;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;

import org.h2.tools.RunScript;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import fr.cea.organicity.manager.ManagerApplication;
import lombok.extern.log4j.Log4j;

@Log4j
@Component
public class DataLoader {

	@Autowired
	private DataSource datasource;

	@Value("${spring.datasource.username}")
	private String user;

	@Value("${spring.datasource.password}")
	private String pass;

	@Value("${spring.datasource.load-sample-data}")
	private boolean loadSampleData;

	private static final String FILE_NAME = "sample-data.sql";

	@PostConstruct
	void loadData() throws SQLException {
		if (loadSampleData) {
			Connection conn = datasource.getConnection(user, pass);
			InputStream in = ManagerApplication.class.getClassLoader().getResourceAsStream(FILE_NAME);
			BufferedReader input = new BufferedReader(new InputStreamReader(in));
			RunScript.execute(conn, input);
			log.info("Sample data loaded into database from file " + FILE_NAME);
		}
	}
}
