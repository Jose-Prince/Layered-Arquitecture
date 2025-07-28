import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.LoaderOptions;

public class ConfigLoader {

  public static ProtocolConfig load(String path) throws Exception {
    LoaderOptions options = new LoaderOptions();
    Constructor constructor = new Constructor(ProtocolConfig.class, options);

    Yaml yaml = new Yaml(constructor);

    try (InputStream in = Files.newInputStream(Path.of(path))) {
      return yaml.load(in);
    }
  }
}