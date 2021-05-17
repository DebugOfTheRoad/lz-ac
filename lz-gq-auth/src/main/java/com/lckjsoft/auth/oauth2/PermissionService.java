package com.lckjsoft.auth.oauth2;

import com.lckjsoft.common.util.NullUtil;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.PatternMatchUtils;
import org.springframework.util.StringUtils;

import java.util.Collection;

/**
 * @author uid40330
 */
@Component("permission")
public class PermissionService {
    public boolean hasPermission(String permission) {
        if (NullUtil.isNull(permission)) {
            return false;
        }
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            return false;
        }
        Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		/**/
		for (GrantedAuthority authority : authorities) {
			if(PatternMatchUtils.simpleMatch(permission, authority.getAuthority())){
				return true;
			}
		}
		return false;
//        return authorities.stream()
//                .map(GrantedAuthority::getAuthority)
//                .filter(StringUtils::hasText)
//                .anyMatch(x -> PatternMatchUtils.simpleMatch(permission, x));
    }
}
