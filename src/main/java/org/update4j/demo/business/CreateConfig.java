package org.update4j.demo.business;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.update4j.Configuration;
import org.update4j.FileMetadata;

import static org.update4j.demo.bootstrap.CreateConfig.*;

public class CreateConfig {

	public static void main(String[] args) throws IOException {
		Configuration config = Configuration.builder()
						.baseUri("http://docs.update4j.org/demo/business")
						.basePath("${user.dir}/business")
						.file(FileMetadata.readFrom("config/demo-business-1.0.0.jar")
										.path("demo-business-1.0.0.jar")
										.classpath())
						.file(FileMetadata.readFrom("config/controlsfx-9.0.0.jar")
										.uri(mavenUrl("org.controlsfx", "controlsfx", "9.0.0"))
										.classpath())
						.file(FileMetadata.readFrom("config/jfoenix-9.0.8.jar")
										.uri(mavenUrl("com.jfoenix", "jfoenix", "9.0.8"))
										.classpath())
						.file(FileMetadata.readFrom("config/jfxtras-common-10.0-r1.jar")
										.uri(mavenUrl("org.jfxtras", "jfxtras-common", "10.0-r1"))
										.classpath())
						.file(FileMetadata.readFrom("config/jfxtras-gauge-linear-10.0-r1.jar")
										.uri(mavenUrl("org.jfxtras", "jfxtras-gauge-linear", "10.0-r1"))
										.classpath())
						.property("maven.central", MAVEN_BASE)
						.build();

		try (Writer out = Files.newBufferedWriter(Paths.get("config/config.xml"))) {
			config.write(out);
		}
	}
}
