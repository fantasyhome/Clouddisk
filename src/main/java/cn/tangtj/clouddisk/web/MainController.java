package cn.tangtj.clouddisk.web;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
@RequestMapping()
public class MainController {

    //将主页重定向到login.jsp
    @RequestMapping()
    public String index(){
        return "redirect:/login";
    }
}
