package uz.pdp.appjwtdbemailaudit.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import uz.pdp.appjwtdbemailaudit.entity.User;
import uz.pdp.appjwtdbemailaudit.entity.enums.RoleName;
import uz.pdp.appjwtdbemailaudit.payload.ApiResponse;
import uz.pdp.appjwtdbemailaudit.payload.LoginDto;
import uz.pdp.appjwtdbemailaudit.payload.RegisterDto;
import uz.pdp.appjwtdbemailaudit.repository.RoleRepository;
import uz.pdp.appjwtdbemailaudit.repository.UserRepository;
import uz.pdp.appjwtdbemailaudit.security.JwtProvider;

import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

@Service
public class AuthService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;
    @Autowired
    PasswordEncoder passwordEncoder;
    @Autowired
    RoleRepository roleRepository;
    @Autowired
    JavaMailSender javaMailSender;
    @Autowired
    AuthenticationManager authenticationManager;
    @Autowired
    JwtProvider jwtProvider;

    public ApiResponse registerUser(RegisterDto registerDto) {
        if (userRepository.existsByEmail(registerDto.getEmail()))
            return new ApiResponse("Bunday email allaqachon mavjud !", false);
        User user = new User();
        user.setFirstName(registerDto.getFirstName());
        user.setLastName(registerDto.getLastName());
        user.setEmail(registerDto.getEmail());
        user.setPassword(passwordEncoder.encode(registerDto.getPassword()));
        user.setRoles(Collections.singleton(roleRepository.findByRoleName(RoleName.ROLE_USER)));
        user.setEmailCode(UUID.randomUUID().toString());
        userRepository.save(user);

        sendEmail(user.getEmail(), user.getEmailCode());
        return new ApiResponse("Muvaffaqqiyatli ro'yxatdan o'tdingiz !\n" +
                "Akkountingizni aktivlashtirish uchun emailingizni tasdiqlang !", true);
    }

    public Boolean sendEmail(String sendingEmail, String emailCode) {
        try {
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setFrom("pdpUz@gmail.com");
            mailMessage.setTo(sendingEmail);
            mailMessage.setSubject("Akkauntni tasdiqlash");
            mailMessage.setText("<a href = 'http://localhost:8080/api/auth/verifyEmail?emailCode="
                    + emailCode + "&email=" + sendingEmail + "'>Tasdiqlash</a>");
            javaMailSender.send(mailMessage);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public ApiResponse verifyEmail(String email, String emailCode) {
        Optional<User> optionalUser = userRepository.findByEmailAndEmailCode(email, emailCode);
        if (!optionalUser.isPresent()) return new ApiResponse("Bunday akkaunt allaqachon tasdiqlangan !", false);
        User user = optionalUser.get();
        user.setEnabled(true);
        user.setEmailCode(null);
        return new ApiResponse("Akkaunt muvaffaqqiyatli tasdiqlandi !", true);
    }

    public ApiResponse login(LoginDto loginDto) {
        try {
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getUsername(), loginDto.getPassword()));
            User user = (User) authentication.getPrincipal();
            String token = jwtProvider.generateToken(loginDto.getUsername(), user.getRoles());
            return new ApiResponse("Token ketdi", true, token);
        } catch (BadCredentialsException badCredentialsException) {
            return new ApiResponse("Parol yoki login xato !", false);
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
//        Optional<User> optionalUser = userRepository.findByEmail(username);
//        if (optionalUser.isPresent()) return optionalUser.get();
//        throw new UsernameNotFoundException(username + " topilmadi !");
        return userRepository.findByEmail(username).orElseThrow(() -> new UsernameNotFoundException(username + " topilmadi !"));
    }
}
