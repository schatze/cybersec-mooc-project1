package sec.project.config;

import java.util.Arrays;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import sec.project.domain.Account;
import sec.project.repository.AccountRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @PostConstruct
    public void init() {
        Account account = new Account();
        account.setUsername("admin");
        account.setAdmin(true);
        account.setPassword("5ebe2294ecd0e0f08eab7690d2a6ee69");
        accountRepository.save(account);
        
        account = new Account();
        account.setUsername("sysadmin");
        account.setAdmin(true);
        account.setPassword("ea847988ba59727dbf4e34ee75726dc3");
        accountRepository.save(account);
        
        account = new Account();
        account.setUsername("root");
        account.setAdmin(true);
        account.setPassword("59fd0592f5bdd835e6a4ace38e714a0f");
        accountRepository.save(account);
        
        account = new Account();
        account.setUsername("userkitty");
        account.setAdmin(false);
        account.setPassword("e2a7106f1cc8bb1e1318df70aa0a3540");
        accountRepository.save(account);
        
        account = new Account();
        account.setUsername("usercat");
        account.setPassword("5f4dcc3b5aa765d61d8327deb882cf99");
        accountRepository.save(account);
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Account account = accountRepository.findByUsername(username);
        if (account == null) {
            throw new UsernameNotFoundException("No such user: " + username);
        }

        return new org.springframework.security.core.userdetails.User(
                account.getUsername(),
                account.getPassword(),
                true,
                true,
                true,
                true,
                Arrays.asList(new SimpleGrantedAuthority("USER")));
    }
}
