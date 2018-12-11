package org.update4j.demo.business;

import java.io.IOException;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.update4j.Configuration;
import org.update4j.FileMetadata;

public class CreateConfig {

	public static void main(String[] args) throws IOException {
		Configuration config = Configuration.builder().baseUri("http://docs.update4j.org/demo/business")
				.basePath("${user.dir}/business")
				.files(FileMetadata.streamDirectory("config")
						.filter(f -> !f.getSource().toString().endsWith("config.xml")).peek(f -> f.classpath()))
				.build();

		try (Writer out = Files.newBufferedWriter(Paths.get("config/config.xml"))) {
			config.write(out);
		}
	}
}
