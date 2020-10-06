package com.rashaik;

import org.apache.flink.api.common.functions.FilterFunction;
import org.apache.flink.api.common.functions.FlatMapFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.utils.ParameterTool;
import org.apache.flink.util.Collector;

public class WordCount3 {

	public static void main(String[] args) throws Exception  {
		final ParameterTool params = ParameterTool.fromArgs(args);
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		env.getConfig().setGlobalJobParameters(params);
		
		DataSet<String> text = env.readTextFile(params.get("input"));
		
		DataSet<Tuple2<String, Integer>> counts1 = text.filter(t -> t.startsWith("N"))
				.map(new MyMapFunction())
				.groupBy(0)
				.sum(1);
		
		DataSet<Tuple2<String, Integer>> counts2 = text.flatMap(new MyFlatMapFunction())
				.groupBy(0)
				.sum(1);
		
		DataSet<Tuple2<String, Integer>> counts3 = text
			.flatMap(new MyFlatMapFunction())
			.filter(new MyFilterFunction())
			.groupBy(0)
			.sum(1);
		
		if (params.has("output")) {
			counts3.writeAsCsv(params.get("output"), "\n", " ");
			env.execute("WordCount startsWith");
		} else {
			counts3.print();
		}
	}

}

final class MyMapFunction implements MapFunction<String, Tuple2<String, Integer>> {

	@Override
	public Tuple2<String, Integer> map(String value) throws Exception {
		return new Tuple2<String, Integer>(value, 1);
	}
	
}

final class MyFlatMapFunction implements FlatMapFunction<String, Tuple2<String, Integer>> {

	@Override
	public void flatMap(String value, Collector<Tuple2<String, Integer>> out) throws Exception {
		String[] tokens = value.split("\\W+");
		for (String token : tokens) {
			if (token.length() > 0) {
				out.collect(new Tuple2<>(token, 1));
			}
		}
	}
}

final class MyFilterFunction implements FilterFunction<Tuple2<String, Integer>> {

	@Override
	public boolean filter(Tuple2<String, Integer> value) throws Exception {
		return value.f0.startsWith("N");
	}
	
}
