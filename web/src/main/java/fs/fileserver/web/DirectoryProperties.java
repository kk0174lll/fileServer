package fs.fileserver.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import utils.Utils;

@Component
@ConfigurationProperties ("directory")
public class DirectoryProperties
{
  private String work;

  public String getWork()
  {
    return Utils.stringValue(work);
  }

  public void setWork(String work)
  {
    this.work = work;
  }

}
