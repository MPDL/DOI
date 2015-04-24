package de.mpg.mpdl.doi.security.spring;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.inject.Inject;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.core.userdetails.jdbc.JdbcDaoImpl;

public class DoxiUserDetailService extends JdbcDaoImpl implements UserDetailsService {
	

	
	private Logger logger = LogManager.getLogger();
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		logger.info("doxiUserDetailService called!!!");
		return super.loadUserByUsername(username);
	}
	
	@Override
	protected List<UserDetails> loadUsersByUsername(String username) {
		return getJdbcTemplate().query(getUsersByUsernameQuery(), new String[] { username },
				new RowMapper<UserDetails>() {
					public UserDetails mapRow(ResultSet rs, int rowNum)
							throws SQLException {
						String username = rs.getString(1);
						String password = rs.getString(2);
						boolean enabled = rs.getBoolean(3);
						String email = rs.getString(4);
						String prefix = rs.getString(5);
						
						return new DoxiUser(username, password, enabled, email, prefix, true, true, true,
								AuthorityUtils.NO_AUTHORITIES);
					}

				});
	}
	
	@Override
	protected UserDetails createUserDetails(String username,
			UserDetails userFromUserQuery, List<GrantedAuthority> combinedAuthorities) {
		DoxiUser doxiUser = (DoxiUser)userFromUserQuery;
		
		String returnUsername = userFromUserQuery.getUsername();

		/*
		if (!usernameBasedPrimaryKey) {
			returnUsername = username;
		}
		 */
		
		return new DoxiUser(returnUsername, userFromUserQuery.getPassword(),
				userFromUserQuery.isEnabled(), doxiUser.getEmail(), doxiUser.getPrefix(), true, true, true, combinedAuthorities);
	}

}
