package com.fullstackmaina.streamService.serice;

import com.opencsv.CSVWriter;
import org.apache.kafka.streams.kstream.KStream;
import org.encog.ConsoleStatusReportable;
import org.encog.ml.MLRegression;
import org.encog.ml.data.MLData;
import org.encog.ml.data.versatile.NormalizationHelper;
import org.encog.ml.data.versatile.VersatileMLDataSet;
import org.encog.ml.data.versatile.columns.ColumnDefinition;
import org.encog.ml.data.versatile.columns.ColumnType;
import org.encog.ml.data.versatile.sources.CSVDataSource;
import org.encog.ml.data.versatile.sources.VersatileDataSource;
import org.encog.ml.factory.MLMethodFactory;
import org.encog.ml.model.EncogModel;
import org.encog.util.csv.CSVFormat;
import org.encog.util.simple.EncogUtility;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Service;

import com.fullstackmaina.streamService.bindings.StreamBindings;
import com.fullstackmaina.streamService.model.Diabete;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@EnableBinding(StreamBindings.class)
@Service
public class StreamService {

	List<String[]> list = new ArrayList<>();
	MLRegression bestMethod;
	NormalizationHelper helper;
	@Autowired
	KafkaTemplate<Integer, String> template;
	String topicName = "diabetes-result";
	String topicName2 = "diabetes-statistics";


	@StreamListener("diabete-input-channel")
	@SendTo("diabete-positive-output-channel")
	public KStream<String, Diabete>  positive(KStream<String, Diabete> diabete) {

		    String[] header = {"Pregnancies", "Glucose", "BloodPressure", "SkinThickness","Insulin","BMI","DiabetesPedigreeFunction","Age"};
		    list.add(header);

			diabete.foreach((k,v)-> {
				list.add(v.line());
				try {
					CSVWriter writer = new CSVWriter(new FileWriter("diabetes.csv"));
					writer.writeAll(list);
				} catch (IOException e) {
					System.out.println("error");
				}
			});

		return diabete;
	}
	 public void createModelForDiabetes(){
		VersatileDataSource source = new CSVDataSource(
				new File("diabetes.csv"), true  ,
				CSVFormat.DECIMAL_POINT) ;

		VersatileMLDataSet data = new VersatileMLDataSet ( source ) ;
		data.defineSourceColumn (
				"Pregnancies",0 ,  ColumnType.continuous ) ;
		data.defineSourceColumn (
				"Glucose",1 ,  ColumnType.continuous ) ;
		data.defineSourceColumn (
				"BloodPressure",2 ,  ColumnType.continuous ) ;
		data.defineSourceColumn (
				"SkinThickness",3 ,  ColumnType.continuous ) ;
		data.defineSourceColumn (
				"Insulin",4 , ColumnType.continuous ) ;
		data.defineSourceColumn (
				"BMI",5 ,  ColumnType.continuous ) ;
		data.defineSourceColumn (
				"DiabetesPedigreeFunction",6 ,  ColumnType.continuous ) ;
		data.defineSourceColumn (
				"Age",7 ,  ColumnType.continuous ) ;

		ColumnDefinition outputColumn = data.defineSourceColumn ("Outcome",8 , ColumnType.nominal ) ;
		data.analyze() ;
		data.defineSingleOutputOthersInput ( outputColumn ) ;

		EncogModel model = new EncogModel( data ) ;

		model.selectMethod ( data , MLMethodFactory.TYPE_SVM) ;
		model.setReport (new ConsoleStatusReportable( ) ) ;
		data.normalize ();

		model.holdBackValidation ( 0.2 , true , 1001 ) ;
		model.selectTrainingType ( data ) ;
		bestMethod = (MLRegression)model.crossvalidate ( 5 , true ) ;


		System.out.println ( "Training error : "
				+ EncogUtility.calculateRegressionError ( bestMethod ,
				model.getTrainingDataset ( ) ) ) ;
		System.out.println ( "Validation error :"
				+ EncogUtility.calculateRegressionError ( bestMethod ,
				model .getValidationDataset ( ) ) ) ;
		helper = data . getNormHelper ( ) ;
		System.out.println( helper.toString ( ) ) ;
		//System.out.println ( " Final model : " + bestMethod ) ;

		 template.send(topicName2, 0,helper.getInputColumns().get(0).toString());
		 try{
			 Thread.sleep(100);}
		 catch (Exception e){
			 System.out.println("error");
		 }
		 template.send(topicName2, 1,helper.getInputColumns().get(1).toString());
		 try{
			 Thread.sleep(100);}
		 catch (Exception e){
			 System.out.println("error");
		 }
		 template.send(topicName2, 2,helper.getInputColumns().get(2).toString());
		 try{
			 Thread.sleep(100);}
		 catch (Exception e){
			 System.out.println("error");
		 }
		 template.send(topicName2, 3,helper.getInputColumns().get(3).toString());
		 try{
			 Thread.sleep(100);}
		 catch (Exception e){
			 System.out.println("error");
		 }
		 template.send(topicName2, 4,helper.getInputColumns().get(4).toString());
		 try{
			 Thread.sleep(100);}
		 catch (Exception e){
			 System.out.println("error");
		 }
		 template.send(topicName2, 5,helper.getInputColumns().get(5).toString());
		 try{
			 Thread.sleep(100);}
		 catch (Exception e){
			 System.out.println("error");
		 }
		 template.send(topicName2, 6,helper.getInputColumns().get(6).toString());
		 try{
			 Thread.sleep(100);}
		 catch (Exception e){
			 System.out.println("error");
		 }
		 template.send(topicName2, 7,helper.getInputColumns().get(7).toString());
		 try{
			 Thread.sleep(100);}
		 catch (Exception e){
			 System.out.println("error");
		 }
		 template.send(topicName2, 8,helper.getOutputColumns().get(0).toString());

	}

	public void predict(Diabete diabete){
		MLData input = helper.allocateInputVector();
		StringBuilder result = new StringBuilder();
		String [] line =  new String[8];
		line[0]= diabete.getPreg();
		line[1]= diabete.getPlas();
		line[2]= diabete.getPres();
		line[3]= diabete.getSkin();
		line[4]= diabete.getInsu();
		line[5]= diabete.getMass();
		line[6]= diabete.getPedi();
		line[7]= diabete.getAge();
		String correct = diabete.getClasse();

		helper.normalizeInputVector(line,input.getData(),false);
		MLData output = bestMethod.compute(input);

		String dChosen = helper.denormalizeOutputVectorToString(output)[0];
		result.append(Arrays.toString(line));
		result.append(" => predicted : ");
		result.append(dChosen);
		result.append("(Correct : ");
		result.append(correct);
		result.append(")");
		System.out.println(result.toString());

		template.send(topicName, 0,result.toString());
	}
	@Bean
	public CorsFilter corsFilter() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		final CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(true);
		config.addAllowedOrigin("http://172.18.0.2:3000/");
		config.addAllowedOrigin("http://localhost:3000/");
		config.addAllowedHeader("*");
		config.addAllowedMethod("*");
		source.registerCorsConfiguration("/**", config);
		return new CorsFilter(source);
	}

}
