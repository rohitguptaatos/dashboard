package uk.co.aegon.template;

import static org.assertj.core.api.Assertions.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Test;

public class SampleTest {

	//@Test
	public void assertExample() {
		List<String> list = new ArrayList<>();
		list.add("one");
		list.add("two");
		assertThat(true);
		assertThat(list).hasSize(2);
		assertThat(list).containsExactly("one","two");
	}
}
