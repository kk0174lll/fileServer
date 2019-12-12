package fs.fileserver.web;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import utils.Utils;

@Component
@ConfigurationProperties ("server")
public class FileServerProperties
{
  private String directory;

  private String title;

  private String domain;

  public String getDirectory()
  {
    return Utils.stringValue(directory);
  }

  public void setDirectory(String directory)
  {
    this.directory = directory;
  }

  public String getTitle() {
    return Utils.stringValue(title);
  }

  public void setTitle(String title) {
    this.title = title;
  }

  public String getDomain() {
    return Utils.stringValue(domain);
  }

  public void setDomain(String domain) {
    this.domain = domain;
  }
}
