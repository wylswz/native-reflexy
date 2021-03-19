import java.io.File;
import java.sql.Ref;

import com.xmbsmdsj.plugin.ReflectionMojo;
import org.apache.maven.plugin.testing.AbstractMojoTestCase;

public class ReflectionMojoTest extends AbstractMojoTestCase {

	protected void setUp() throws Exception {
		super.setUp();
	}

	public void testGoal() throws Exception {
		File testPom = new File(ReflectionMojoTest.class.getResource("test-pom.xml").getFile());
		ReflectionMojo refMojo = (ReflectionMojo) lookupMojo("reflection", testPom);
		refMojo.execute();
	}
}
