package cn.tangtj.clouddisk.web;

import cn.tangtj.clouddisk.entity.User;
import cn.tangtj.clouddisk.utils.UserUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.mvc.support.RedirectAttributesModelMap;

import javax.servlet.http.HttpServletRequest;

/**
 * 用户登录 和 退出
 */
@Controller
public class LoginController {

    private static Logger logger = LogManager.getLogger(LoginController.class.getName());

    //1、从浏览器get请求，返回login.jsp
    @RequestMapping(value = "/login",method = RequestMethod.GET)
    public String login(){
        logger.error("用户访问");
        return "login";
    }

    //2、用户输入后点登陆，发送post请求表单
    @RequestMapping(value = "/login",method = RequestMethod.POST)
    public String userLogin(HttpServletRequest request, RedirectAttributesModelMap modelMap){
        //3、使用UserUtil获得用户名
        User user = UserUtil.getPrincipal();

        //10、认证结束，跳转FileController
        if (user != null) {
            return "redirect:/file";
        }
        String message = (String) request.getAttribute("message");
        //message不为空，设置message
        if (!"".equals(message)) {
            message = "帐号或密码错误，请重试。";

        }
        //重定向时，以get的方式添加在重定向url之后
        modelMap.addFlashAttribute("message", message);
        return "redirect:/login";
    }

}
