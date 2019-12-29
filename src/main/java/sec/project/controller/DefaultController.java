package sec.project.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.authentication.encoding.MessageDigestPasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import sec.project.domain.Account;
import sec.project.domain.Signup;
import sec.project.repository.AccountRepository;
import sec.project.repository.SignupRepository;

@Controller
public class DefaultController {

    @Autowired
    private SignupRepository signupRepository;

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private UserDetailsService userDetailsService;

    @RequestMapping("*")
    public String defaultMapping() {
        return "redirect:/form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.GET)
    public String loadForm(Model model) {

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("User: " + auth.getName() + " got default page.");

        Boolean isAdmin = accountRepository.findByUsername(auth.getName()).getAdmin();
        model.addAttribute("admin", isAdmin);

        model.addAttribute("user", auth.getName());

        return "form";
    }

    @RequestMapping(value = "/form", method = RequestMethod.POST)
    public String submitForm(@RequestParam String name, @RequestParam String message) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        System.out.println("User: " + auth.getName() + " posted new message.");
        
        //signupRepository.save(new Signup(auth.getName(), message));   // Fix for XSS and user tampered usernames
        
        signupRepository.save(new Signup(name, message));   
        return "redirect:/done";
    }

    @RequestMapping(value = "/done", method = RequestMethod.GET)
    public String loadDone(Model model) {
        model.addAttribute("registers", signupRepository.findAll());
        return "done";
    }
    
    @RequestMapping(value = "/user", method = RequestMethod.POST)
    public String newUser(@RequestParam String username, @RequestParam String password) {
        /*
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();   // Fix for broken access control for adding users
        Boolean isAdmin = accountRepository.findByUsername(auth.getName()).getAdmin();
        if(!isAdmin)      
            return "redirect:/form";*/
        
        MessageDigestPasswordEncoder enc = new Md5PasswordEncoder();
        String hash = enc.encodePassword(password, null);

        System.out.println("New user: \"" + username + "\" password has hash: " + hash);

        Account newAcc = new Account();
        newAcc.setUsername(username);
        newAcc.setPassword(hash);
        accountRepository.save(newAcc);

        return "redirect:/admin";
    }

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public String loginGet() {
        return "login";
    }

    @RequestMapping(value = "/admin", method = RequestMethod.GET)
    public String adminGet(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        Boolean isAdmin = accountRepository.findByUsername(auth.getName()).getAdmin();
        model.addAttribute("admin", isAdmin);
        
        //if(!isAdmin)      // Fix for admin page's broken access control
        //    return "redirect:/form";

        System.out.println("User: " + auth.getName() + " got adminpage.");

        model.addAttribute("users", accountRepository.findAll());
        return "admin";
    }

    @RequestMapping(value = "/login-error", method = RequestMethod.GET)
    public String badLogin() {
        return "login-error";
    }
}
