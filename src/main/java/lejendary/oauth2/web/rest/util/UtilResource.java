package lejendary.oauth2.web.rest.util;

import lejendary.oauth2.repository.util.UtilRepository;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.inject.Inject;
import java.util.Date;

/**
 * @author Jonathan Leijendekker
 *         Date: 8/23/2016
 *         Time: 3:47 PM
 */

@RestController
@RequestMapping("api")
public class UtilResource {

    private final UtilRepository utilRepository;

    @Inject
    public UtilResource(UtilRepository utilRepository) {
        this.utilRepository = utilRepository;
    }

    @RequestMapping(
            value = "database-date",
            method = RequestMethod.GET,
            produces = MediaType.TEXT_PLAIN_VALUE
    )
    public Date getDatabaseDate() {
        return utilRepository.getDatabaseDate();
    }

}
