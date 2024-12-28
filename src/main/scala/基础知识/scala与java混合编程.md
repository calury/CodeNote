

scala和Java混合编译的时候会出现问题，如果Java中引用scala的包，那么会在编译的时候报错找不到相关类

原因
这种问题是因为没有先编译scala，在编译Java的时候找不到scala的类。

使用scala-maven插件;
<plugins>
<plugin>
<groupId>net.alchim31.maven</groupId>
<artifactId>scala-maven-plugin</artifactId>
<version>3.2.0</version>
<executions>
<execution>
<id>compile-scala</id>
<phase>compile</phase>
<goals>
<goal>add-source</goal>
<goal>compile</goal>
</goals>
</execution>
<execution>
<id>test-compile-scala</id>
<phase>test-compile</phase>
<goals>
<goal>add-source</goal>
<goal>testCompile</goal>
</goals>
</execution>
</executions>
<configuration>
<scalaVersion>${scala.version}</scalaVersion>
</configuration>
</plugin>
</plugins>




仅参考二:





<build>
	<plugins>
		<plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-shade-plugin</artifactId>
			<version>2.3</version>
			<executions>
				<execution>
					<phase>package</phase>
					<goals>
						<goal>shade</goal>
					</goals>
					<configuration>
						<artifactSet>
							<excludes>
								<exclude>org.apache.flink:*</exclude>
								<exclude>org.slf4j:*</exclude>
								<exclude>log4j:*</exclude>
							</excludes>
						</artifactSet>
						<transformers>
							<transformer
									implementation="org.apache.maven.plugins.shade.resource.ManifestResourceTransformer">
								<mainClass>XXX.XXX.XXX.XXX.DataParsingWorker</mainClass>  <!--这里运行类！ -->
							</transformer>
						</transformers>
					</configuration>
				</execution>
			</executions>
		</plugin>
		<plugin>
			<groupId>net.alchim31.maven</groupId>
			<artifactId>scala-maven-plugin</artifactId>
			<executions>
				<execution>
					<id>scala-compile</id>
					<phase>compile</phase>
					<goals>
						<goal>add-source</goal>
						<goal>compile</goal>
					</goals>
				</execution>
				<execution>
					<id>scala-test-compile</id>
					<phase>test-compile</phase>
					<goals>
						<goal>testCompile</goal>
					</goals>
				</execution>
			</executions>
		</plugin>
		<plugin>
			<artifactId>maven-compiler-plugin</artifactId>
			<executions>
				<execution>
					<id>default-compile</id>
					<phase>none</phase>
				</execution>
				<execution>
					<id>default-testCompile</id>
					<phase>none</phase>
				</execution>
			</executions>
		</plugin>

		<!-- Eclipse Scala Integration -->
		<plugin>
			<groupId>org.apache.maven.plugins</groupId>
			<artifactId>maven-eclipse-plugin</artifactId>
			<version>2.8</version>
			<configuration>
				<downloadSources>true</downloadSources>
				<projectnatures>
					<projectnature>org.scala-ide.sdt.core.scalanature</projectnature>
					<projectnature>org.eclipse.jdt.core.javanature</projectnature>
				</projectnatures>
				<buildcommands>
					<buildcommand>org.scala-ide.sdt.core.scalabuilder</buildcommand>
				</buildcommands>
				<classpathContainers>
					<classpathContainer>org.scala-ide.sdt.launching.SCALA_CONTAINER</classpathContainer>
					<classpathContainer>org.eclipse.jdt.launching.JRE_CONTAINER</classpathContainer>
				</classpathContainers>
				<excludes>
					<exclude>org.scala-lang:scala-library</exclude>
					<exclude>org.scala-lang:scala-compiler</exclude>
				</excludes>
				<sourceIncludes>
					<sourceInclude>**/*.scala</sourceInclude>
					<sourceInclude>**/*.java</sourceInclude>
				</sourceIncludes>
			</configuration>
		</plugin>
		<plugin>
			<groupId>org.codehaus.mojo</groupId>
			<artifactId>build-helper-maven-plugin</artifactId>
			<version>1.7</version>
			<executions>
				<!-- Add src/main/scala to eclipse build path -->
				<execution>
					<id>add-source</id>
					<phase>generate-sources</phase>
					<goals>
						<goal>add-source</goal>
					</goals>
					<configuration>
						<sources>
							<source>src/main/scala</source>
						</sources>
					</configuration>
				</execution>
				<!-- Add src/test/scala to eclipse build path -->
				<execution>
					<id>add-test-source</id>
					<phase>generate-test-sources</phase>
					<goals>
						<goal>add-test-source</goal>
					</goals>
					<configuration>
						<sources>
							<source>src/test/scala</source>
						</sources>
					</configuration>
				</execution>
			</executions>
		</plugin>
	</plugins>
</build>