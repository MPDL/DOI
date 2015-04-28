package de.mpg.mpdl.doi.model;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

/**
 * Data access object an incremental id, which is stored in a database
 * @author walter
 *
 */
@Component
@Scope(value = "singleton")
public class UniqueInkrementedIdDao {
	
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	public void setDataSource(DataSource dataSource) {
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Transactional
	public synchronized long getNextDoi() {
		long identifier = jdbcTemplate.queryForObject("select unique_id from unique_identifier where id = 1", Integer.class);
		identifier++;
		jdbcTemplate.update("update unique_identifier set unique_id = ? where id = 1", identifier);
		return identifier;
	}
}
