package fs.fileserver.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import utils.Utils;

@Component
@ConfigurationProperties ("directory")
public class DirectoryProperties
{
  private String work;
  private String prepare;

  public String getWork()
  {
    return Utils.stringValue(work);
  }

  public String getPrepare()
  {
    return Utils.stringValue(prepare);
  }

  public void setWork(String work)
  {
    this.work = work;
  }

  public void setPrepare(String prepare)
  {
    this.prepare = prepare;
  }
}
