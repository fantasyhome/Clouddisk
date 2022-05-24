package cn.tangtj.clouddisk.utils;

import cn.tangtj.clouddisk.entity.User;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.subject.Subject;


public class UserUtil {

    public static User getPrincipal() {

        //4、委托SecurityUtils获得用户名
        //5、SecurityUtils委托SecurityManager，再委托Authenticator
        Subject subject = SecurityUtils.getSubject();
        User user = (User) subject.getPrincipal();
        if (user != null) {
            return user;
        }
        return null;
    }
}
