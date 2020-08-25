package com.bikash.bikashBackend.Service.mailService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MailController {
    private final MailService mailService;
    @Autowired
    ResourceLoader resourceLoader;

    public MailController(MailService mailService) {
        this.mailService = mailService;
    }
    @GetMapping("/send")
    public EmailStatus sendEMail(){
        String to[]={"golam62@dipti.com.bd"};//jake mail pathabe
       return mailService.sendNonHtmlMail(to,"spring boot mail test ","Brother, if you got my mail,please  let me know");
    }
    @GetMapping("/sendHtmlMail")//for html direct
    public EmailStatus sendHtmlEMail(){
        String to[]={"golamkibria041@gmail.com","delowarhossain21212@gmail.com"};//jake mail pathabe
       return mailService.sendHtmlMail(to,"spring boot mail test ","<!DOCTYPE html>\n" +
               "<html lang=\"en\" xmlns:th=\"http://www.thymeleaf.org\">\n" +
               "<head>\n" +
               "    <meta charset=\"UTF-8\">\n" +
               "    <title>Title</title>\n" +
               "</head>\n" +
               "<body>\n" +
               "<div align=\"center\">\n" +
               "    <h1 style=\"color: green;font-size: x-large\">Hi i am from spring boot mail sender</h1>\n" +
               "</div>\n" +
               "</body>\n" +
               "</html>");
    }
   /* @GetMapping("/sendHtmlMailFromHtml From")//for  html  mail from html foile
    public EmailStatus sendhtmlEMailFromHtmlFile() throws IOException {
        String to[]={"golamkibria041@gmail.com"};//jake mail pathabe
        //Resource resource= (Resource) resourceLoader.getResource("classpath:index.html").getFile();
        File file = new File(
                getClass().getClassLoader().getResource("classpath:index.html").getFile()
        );

        return mailService.sendHtmlMail(to,"This is Test hrml file message From my spring boot intellij idea project", String.valueOf(file.list()));
    }
*/


}
