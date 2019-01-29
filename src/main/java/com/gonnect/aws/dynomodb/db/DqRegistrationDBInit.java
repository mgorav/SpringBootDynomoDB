package com.gonnect.aws.dynomodb.db;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.model.*;
import com.gonnect.aws.dynomodb.dq.model.DqRegistration;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class DqRegistrationDBInit implements ApplicationListener<ContextRefreshedEvent> {


  @Autowired
  private DynamoDBMapper dbMapper;

  @Autowired
  private AmazonDynamoDB dynamoDB;

  @Override
  public void onApplicationEvent(ContextRefreshedEvent event) {

    log.trace("Entering createDatabaseTablesIfNotExist()");
    CreateTableRequest request = dbMapper
        .generateCreateTableRequest(DqRegistration.class)
        .withProvisionedThroughput(new ProvisionedThroughput(1L, 1L));
    try {
      DescribeTableResult result = dynamoDB.describeTable(request.getTableName());
      log.info("Table status {}, {}", request.getTableName(), result.getTable().getTableStatus());
    } catch (ResourceNotFoundException expectedException) {
      CreateTableResult result = dynamoDB.createTable(request);
      log.info("Table creation triggered {}, {}", request.getTableName(), result.getTableDescription().getTableStatus());
    }
  }

}
