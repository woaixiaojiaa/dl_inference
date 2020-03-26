package com.bj58.ailab.dlpredictonline.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.tensorflow.framework.DataType;
import org.tensorflow.framework.TensorProto;
import org.tensorflow.framework.TensorShapeProto;
import tensorflow.serving.Model;
import tensorflow.serving.Predict;
import tensorflow.serving.Predict.PredictRequest;
import tensorflow.serving.Predict.PredictResponse;

/**
 * Tensorflow 模型示例
 * request生成，response解析
 * @author 58
 **/
public class TensorflowClient {

    public PredictRequest getRequest(){
        // 创建请求
        Predict.PredictRequest.Builder predictRequestBuilder = Predict.PredictRequest.newBuilder();
        //　添加模型相关参数
        // 在线预测任务对应的模型名称,与tensorflow-serving启动参数对应
        // 注意，modelname末尾必须包含taskid值
        String modelname = "lstm-m-69";
        Model.ModelSpec.Builder modelTensorBuilder = Model.ModelSpec.newBuilder().setName(modelname);
        // 模型导出时签名参数
        String signatureName = "prediction";
        // 若没有签名这两行可去掉,使用默认值
        modelTensorBuilder.setSignatureName(signatureName);
        // 模型信息添加到request请求中
        predictRequestBuilder.setModelSpec(modelTensorBuilder.build());
        // 填充数据
        TensorProto.Builder tensorProtoBuilder = TensorProto.newBuilder();
        // 数据类型 DataType为枚举类型
        // 这里假设模型数据类型为 Int32
        tensorProtoBuilder.setDtype(DataType.DT_INT32);
        // valueList中数据类型应该与DataType一致
        List<Integer> valueList = new ArrayList<Integer>();
        // 填充数据，valueList需要人为填充数据
        // 注意函数入参数据类型
        tensorProtoBuilder.addAllIntVal(valueList);
        // 1. 这里不是Tensor（也就是Scalar）就不要setSize(),不需要tensorShapeBuilder
        // 2. 多维的Tensor，需要定义tensorShapeBuilder，按顺序添加size
        // 定义数据维度
        TensorShapeProto.Builder tensorShapeBuilder = TensorShapeProto.newBuilder();
        //valueList数据内容为单个数,valueList长度为1
        tensorShapeBuilder.addDim(TensorShapeProto.Dim.newBuilder().setSize(1));
        //valueList数据内容为2维数据,valueList长度为1*10=10
        tensorShapeBuilder.addDim(TensorShapeProto.Dim.newBuilder().setSize(10));
        //valueList数据内容为3维数据,valueList长度为1*10*100=1000
        tensorShapeBuilder.addDim(TensorShapeProto.Dim.newBuilder().setSize(100));
        tensorProtoBuilder.setTensorShape(tensorShapeBuilder.build());
        predictRequestBuilder.putInputs("image", tensorProtoBuilder.build());
        //putInputs key对应模型导出：signature_def_map中inputs={"image": tensor_info_x}的image

        // 如果有多个putInputs key重复以上代码
        // 例如inputs={'input':tensor_info_x, 'sen_len':tensor_info_len}
        // 还需要predictRequestBuilder.putInputs("sen_len", tensorProtoBuilder.build());

        // request对象
        PredictRequest request = predictRequestBuilder.build();
        return request;
    }

    public void printResult(PredictResponse response){
        if (response == null){
            System.out.println("response is null");
            return;
        }
        Map<String, TensorProto> outputsMap = response.getOutputsMap();
        if (outputsMap == null || outputsMap.isEmpty()){
            System.out.println("outputsMap is null");
            return;
        }
        // 这里假设模型输出key="y"，输出类型为List<Float>数据
        // 打印输出数据
        String key = "y";
        if (outputsMap.containsKey(key)) {
            TensorProto tensorProto = outputsMap.get(key);
            for (Float f : tensorProto.getFloatValList()) {
                System.out.println(f);
            }
        }
    }
}
