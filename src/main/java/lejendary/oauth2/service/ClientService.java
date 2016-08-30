package lejendary.oauth2.service;

import lejendary.oauth2.repository.ClientRepository;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.ClientDetailsService;
import org.springframework.security.oauth2.provider.ClientRegistrationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * @author Jonathan Leijendekker
 *         Date: 8/26/2016
 *         Time: 4:37 PM
 */

@Service
@Transactional
public class ClientService implements ClientDetailsService {

    private final ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @Override
    public ClientDetails loadClientByClientId(String s) throws ClientRegistrationException {
        return Optional.ofNullable(clientRepository.get(s)).orElseThrow(() -> new ClientRegistrationException("Client not found"));
    }
}
