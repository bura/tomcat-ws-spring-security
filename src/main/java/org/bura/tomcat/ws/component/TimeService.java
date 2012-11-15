package org.bura.tomcat.ws.component;


import java.util.Date;

import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Service;


@Service
public class TimeService {

	@Secured("ROLE_ADMIN")
	public Date getCurrentTime() {
		return new Date();
	}

}
