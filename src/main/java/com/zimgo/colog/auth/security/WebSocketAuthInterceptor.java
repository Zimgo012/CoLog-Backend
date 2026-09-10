package com.zimgo.colog.auth.security;

import com.zimgo.colog.diary.DiaryService;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class WebSocketAuthInterceptor implements ChannelInterceptor {

    private final JWTService jwtService;
    private final UserDetailsService userDetailsService;
//    private final DiaryService diaryService;

    /**
     * Constructs a {@code WebSocketAuthInterceptor} instance with the specified services.
     * This interceptor is responsible for handling authentication and user-diary
     * session binding in a WebSocket context.
     *
     * @param jwtService          The {@code JWTService} used for validating and extracting
     *                            user information from JWT tokens.
     * @param userDetailsService  The {@code UserDetailsService} used for retrieving user
     *                            details based on authentication data.
     *
     */
    public WebSocketAuthInterceptor (JWTService jwtService, UserDetailsService userDetailsService){
        this.jwtService = jwtService;
        this.userDetailsService = userDetailsService;
    }


    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {

        // 1. get header
        StompHeaderAccessor accessor =
                MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        //                StompHeaderAccessor.wrap(message);

        // SAMPLE MESAGE: Command: CONNECT
        //Headers: {
        //    Authorization=[Bearer random123],
        //    accept-version=[1.2,1.1,1.0],
        //    heart-beat=[10000,10000]
        //}

        // 2. check if the command is 'CONNECT'
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            Authentication authentication =
                    authenticate(accessor);

            String diaryIdHeader =
                    accessor.getFirstNativeHeader("diaryId");

            if (diaryIdHeader != null) {
                bindDiary(accessor, authentication);
            }
        }

        return message;
    }

    /**
     * Authenticates a user based on the JWT token provided in the Stomp headers.
     * This method validates the token, extracts the user's details, and associates
     * the authenticated user with the WebSocket session.
     *
     * @param accessor The {@code StompHeaderAccessor} object used to retrieve the
     *                 "Authorization" header containing the JWT token.
     * @return An {@code Authentication} object representing the authenticated user and
     *         their granted authorities.
     * @throws AccessDeniedException If the "Authorization" header is missing, does not
     *                               contain a Bearer token, or if the token is invalid.
     */
    private Authentication authenticate(StompHeaderAccessor accessor){
        // 3. Check authentication
        String authHeader = accessor.getFirstNativeHeader("Authorization");

        // 3-1. make sure to check bearer
        if (authHeader == null || !authHeader.startsWith("Bearer ")){
            throw new AccessDeniedException ("Missing authorization header");
        }

        try{

            // 4. Extract the jwt from authentication
            String jwt = authHeader.substring(7);

            // 5. Extract username using jwtService
            String username = jwtService.extractUsername(jwt);

            // 6. Extract user details
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Check if token is valid
            if(!jwtService.isValidToken(jwt, userDetails)){
                throw new AccessDeniedException("Invalid JWT");
            }

            UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                    userDetails, null, userDetails.getAuthorities()
            );

            accessor.setUser(authentication);


            return authentication;

        }catch (Exception e){
            throw new AccessDeniedException("Invalid JWT");
        }

    }

    /**
     * Associates a user's session with a specific diary based on the provided
     * headers and authentication. Verifies user authorization to access the diary
     * and stores relevant information in the session attributes if access is allowed.
     *
     * @param accessor          The {@code StompHeaderAccessor} object used to retrieve
     *                          native headers and manage session attributes.
     * @param authentication    The {@code Authentication} object containing the user's
     *                          authentication details, including principal information.
     * @throws AccessDeniedException If the "diaryId" header is missing, session attributes are missing,
     *                               or the user is not authorized to access the requested diary.
     */
    private void bindDiary(
            StompHeaderAccessor accessor,
            Authentication authentication
    ) {

        String diaryIdHeader =
                accessor.getFirstNativeHeader("diaryId");

        if (diaryIdHeader == null) {
            throw new AccessDeniedException(
                    "Missing diary ID"
            );
        }

        Long diaryId =
                Long.parseLong(diaryIdHeader);

        CustomUserDetails userDetails =
                (CustomUserDetails)
                        authentication.getPrincipal();

        Long userId =
                userDetails.getId();


        // Store authorization in session
        // ============================================

        Map<String, Object> attributes =
                accessor.getSessionAttributes();

        if (attributes == null) {
            throw new AccessDeniedException(
                    "Session attributes missing"
            );
        }

        attributes.put(
                "DIARY_ID",
                diaryId
        );

        attributes.put(
                "USER_ID",
                userId
        );
    }
}

