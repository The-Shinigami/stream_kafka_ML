package com.fullstackmaina.streamService.bindings;

import org.apache.kafka.streams.kstream.KStream;
import org.springframework.cloud.stream.annotation.Input;
import org.springframework.cloud.stream.annotation.Output;

import com.fullstackmaina.streamService.model.Diabete;

public interface StreamBindings {

	@Input("diabete-input-channel")
	KStream<String, Diabete> inputStream();

	@Output("diabete-positive-output-channel")
	KStream<String, Diabete> positiveStream();


}
