package org.gymapp.backend.security.config

import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.annotation.web.invoke
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter
import org.springframework.security.web.SecurityFilterChain

@Configuration
@EnableWebSecurity
@EnableMethodSecurity(securedEnabled = true, prePostEnabled = true, jsr250Enabled = true)
class WebConfig(
) {

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http.invoke {
            csrf { disable() }
            authorizeHttpRequests {
                authorize("/user", authenticated)
            }
            apply {
                oauth2ResourceServer {
                    jwt {
                        jwtAuthenticationConverter()
                    }
                }
            }
        }
        return http.build()
    }

    fun jwtAuthenticationConverter(): JwtAuthenticationConverter {
        val converter = JwtAuthenticationConverter()
        return converter
    }
}

@Configuration
class JwtDecoderConfig {

    @Value("\${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private lateinit var jwkSetUri: String

    @Bean
    fun jwtDecoder(): NimbusJwtDecoder {
        return NimbusJwtDecoder.withJwkSetUri(this.jwkSetUri).build()
    }
}

//class CustomJwtAuthenticationConverter(
//    private val userDetailsService: CustomUserDetailsService
//) : Converter<Jwt, AbstractAuthenticationToken> {
//
//    override fun convert(jwt: Jwt): AbstractAuthenticationToken {
//        val user = userDetailsService.loadUserByUsername(jwt.claimAsString("email")) // Assuming 'email' is a claim
//        val authorities = user.roles.map { SimpleGrantedAuthority("ROLE_${it.name}") } // Customize this based on your role structure
//        return UsernamePasswordAuthenticationToken(user, jwt, authorities)
//    }
//}