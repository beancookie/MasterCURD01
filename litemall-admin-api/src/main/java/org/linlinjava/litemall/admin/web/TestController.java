package org.linlinjava.litemall.admin.web;

import org.apache.http.HttpRequest;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

@RestController
public class TestController {

       @RequestMapping("/test")
      public void Test(HttpServletRequest httpRequest){
           System.out.print(httpRequest.getHeaders("aa"));

      }


}
