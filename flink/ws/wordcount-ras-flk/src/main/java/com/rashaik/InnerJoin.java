package com.rashaik;

import org.apache.flink.api.common.functions.JoinFunction;
import org.apache.flink.api.common.functions.MapFunction;
import org.apache.flink.api.java.DataSet;
import org.apache.flink.api.java.ExecutionEnvironment;
import org.apache.flink.api.java.tuple.Tuple2;
import org.apache.flink.api.java.tuple.Tuple3;
import org.apache.flink.api.java.utils.ParameterTool;

public class InnerJoin {
	
	public static void main(String[] args) throws Exception {
		
		final ParameterTool params = ParameterTool.fromArgs(args);
		final ExecutionEnvironment env = ExecutionEnvironment.getExecutionEnvironment();
		env.getConfig().setGlobalJobParameters(params);
		
		DataSet<Tuple2<Integer, String>> personSet =
			env.readTextFile(params.get("input1"))
			.map(new MapFunction<String, Tuple2<Integer, String>>() {

				@Override
				public Tuple2<Integer, String> map(String value) throws Exception {
					String[] vals = value.split(",");
					return new Tuple2<>(Integer.parseInt(vals[0]), vals[1]);
				}
				
			});
		
		DataSet<Tuple2<Integer, String>> locationSet =
		env.readTextFile(params.get("input2"))
			.map(new MapFunction<String, Tuple2<Integer, String>>() {

				@Override
				public Tuple2<Integer, String> map(String value) throws Exception {
					String[] vals = value.split(",");
					return new Tuple2<>(Integer.parseInt(vals[0]), vals[1]);
				}
				
			});
		
		DataSet<Tuple3<Integer, String, String>> joinedSet =
			personSet.fullOuterJoin(locationSet)
			.where(0).equalTo(0)
			.with(new JoinFunction<Tuple2<Integer, String>, Tuple2<Integer, String>, Tuple3<Integer, String, String>>() {

				@Override
				public Tuple3<Integer, String, String> join(Tuple2<Integer, String> person,
						Tuple2<Integer, String> location) throws Exception {

					// Left Outer Join
					if (location == null)
						return new Tuple3<>(person.f0, person.f1, "NULL");
					
					// Right Outer Join
					if (person == null)
						return new Tuple3<>(location.f0, "NULL", location.f1);
					
					return new Tuple3<>(person.f0, person.f1, location.f1);
				}
				
			});
		
//		joinedSet.writeAsCsv(params.get("output"), "\n", " ");
//		env.execute("InnerJoin startsWith");
		
		joinedSet.print();
	}

}
