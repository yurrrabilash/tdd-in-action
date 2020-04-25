package org.yura.bilash.params.provider;

import java.util.Arrays;
import java.util.stream.Stream;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsProvider;
import org.junit.jupiter.params.support.AnnotationConsumer;

public class LambdaSeparatorValueArgumentsProvider implements ArgumentsProvider, AnnotationConsumer<LambdaSeparatorValueSource> {
	private static final String LAMBDA = " -> ";
	private LambdaSeparatorValueSource annotation;

	@Override
	public void accept(LambdaSeparatorValueSource lambdaSeparatorValueSource) {
		this.annotation = lambdaSeparatorValueSource;
	}

	@Override
	public Stream<? extends Arguments> provideArguments(ExtensionContext context) throws Exception {
		return Arrays
			.stream(this.annotation.value())
			.map((line) -> Arrays
				.stream(line.split(LAMBDA))
				.map(String::strip).toArray()
			)
			.map(Arguments::of);
	}
}
