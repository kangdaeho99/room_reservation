package com.room.reservation.security.filter;

import com.room.reservation.security.dto.AuthMemberDTO;
import com.room.reservation.security.util.JWTUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.log4j.Log4j2;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;

import java.io.IOException;

@Log4j2
public class ApiLoginFilter extends AbstractAuthenticationProcessingFilter {

    /**
     * JWTUtil의 생성과 검증(validation)에 문제가 없다면 최종적으로 ApiLoginFilter/ApiCheckFilter에 적용해야합니다.
     */
    private JWTUtil jwtUtil;

    /**
     *
     * @param defaultFilterProcessesUrl
     * @param jwtUtil : APILoginFilter는 JWTUTIL을 생성자를 통해서 주입받습니다.
     */
    public ApiLoginFilter(String defaultFilterProcessesUrl, JWTUtil jwtUtil) {
        super(defaultFilterProcessesUrl);
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response) throws AuthenticationException, IOException, ServletException {
        log.info("------------------ApiLoginFilter------------------------");
        log.info("attemptAuthentication");

        String email = request.getParameter("email");
        String pw = request.getParameter("pw");


        if(email == null){
            throw new BadCredentialsException("email cannot be null");
        }
        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(email, pw);

        return getAuthenticationManager().authenticate(authToken);
    }

    /**
     * 인증 성공을 처리합니다.(ApiLoginFailHandler 처럼 따로 클래스를 만들어처리할 수 있지만, AbstractAuthenticationProcessingFilter 클래스에 존재하는
     * successfulAuthentication() 메서드를 override해서 구현합니다.
     * @param request
     * @param response
     * @param chain
     * @param authResult the object returned from the <tt>attemptAuthentication</tt> : 성공한 사용자의 인증정보를 가지고 있는 Authentication 객체.
     *                   이를 통해 인증에 성고한 사용자의 정보를 로그에서 확인합니다. '/api/login?email=ewqeqw&&pw=1111'
     * method.
     * @throws IOException
     * @throws ServletException
     */
    @Override
    protected void successfulAuthentication(HttpServletRequest request, HttpServletResponse response, FilterChain chain, Authentication authResult) throws IOException, ServletException{
        log.info("---------------------ApiLoginFilter------------------------");
        log.info("successfulAuthentication: "+ authResult);

        log.info(authResult.getPrincipal());

        //email Address
        String email = ((AuthMemberDTO) authResult.getPrincipal()).getUsername();
        String token = null;
        try{
            token = jwtUtil.generateToken(email);
            response.setContentType("text/plain");
            response.getOutputStream().write(token.getBytes());
            log.info(token);
        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
