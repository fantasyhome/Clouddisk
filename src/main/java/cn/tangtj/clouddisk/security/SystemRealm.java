package cn.tangtj.clouddisk.security;

import cn.tangtj.clouddisk.entity.User;
import cn.tangtj.clouddisk.service.UserService;
import cn.tangtj.clouddisk.utils.UserUtil;
import cn.tangtj.clouddisk.web.LoginController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.authc.*;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SystemRealm extends AuthorizingRealm {

    private static Logger logger = LogManager.getLogger(LoginController.class.getName());

    private final UserService userService;

    @Autowired
    public SystemRealm(UserService userService) {
        this.userService = userService;
    }

    /**
     * 授权查询回调函数, 进行鉴权但缓存中无用户的授权信息时调用,负责在应用程序中决定用户的访问控制的方法
     */
    @Override
    protected AuthorizationInfo doGetAuthorizationInfo(PrincipalCollection principals) {
        return null;
    }

    /**
     * 认证回调函数，登录信息和用户验证信息验证
     */
    @Override
    protected AuthenticationInfo doGetAuthenticationInfo(AuthenticationToken token) throws AuthenticationException {
        //6、UI封装成的AuthenticationToken，得到数据
        String username = (String) token.getPrincipal();
        String password = new String((char[]) token.getCredentials());
        logger.info("用户 {},密码 {password} 尝试登录",username,password);

        //7、数据库中得到user
        User user = userService.findByName(username);
        if (user == null){
            throw new UnknownAccountException();
        }else {
            //8、登录前先登出，防止用户点回退键，未清空缓存
            User now = UserUtil.getPrincipal();
            if (now != null){
                SecurityUtils.getSubject().logout();
            }
            if (user.getPassword().equals(password)){
                logger.info("用户 {} 登录成功",user.getUsername());
                //9、返回给Authenticator
                return new SimpleAuthenticationInfo(user,password,getName());
            }
        }
        return null;
    }
}
