import java.io.File;

import com.xmbsmdsj.plugin.ProxyMojoAbstract;
import com.xmbsmdsj.plugin.ReflectionMojoAbstract;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class ReflectionMojoTest extends AbstractMojoTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGoal() throws Exception {
		File testPom = new File(ReflectionMojoTest.class.getResource("test-pom.xml").getFile());
		ReflectionMojoAbstract refMojo = (ReflectionMojoAbstract) lookupMojo("reflection", testPom);
		refMojo.execute();
	}

	public void testProxy() throws Exception {
		File testPom = new File(ReflectionMojoTest.class.getResource("test-pom.xml").getFile());
		ProxyMojoAbstract proxyMojo = (ProxyMojoAbstract) lookupMojo("proxy", testPom);
		proxyMojo.execute();
	}
}
