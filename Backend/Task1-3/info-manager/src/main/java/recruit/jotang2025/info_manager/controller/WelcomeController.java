package recruit.jotang2025.info_manager.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

// 控制器类
// 欢迎界面
@RestController
public class WelcomeController {

    @RequestMapping("/")
    public String welcome() {
        return "欢迎来到焦糖二手交易平台！";
    }
}
