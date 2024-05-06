package kissshot1104.personal.blog.global.security.prinipal;

import org.springframework.security.core.userdetails.User;

public class EmptyUser extends User {
    public EmptyUser(){
        super(null,null,null);
    }
}
