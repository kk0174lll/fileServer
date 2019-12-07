package fs.fileserver.web.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class PageController
{
  @GetMapping (value = "/")
  public String main()
  {
    return "main";
  }
}
