/*
 * Copyright (c) 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you
 * may not use this file except in compliance with the License. You may
 * obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 * implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package com.example.cloud.bigtable.helloworld;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.util.Bytes;

import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

import java.io.IOException;
import java.util.Properties;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class HelloServlet extends HttpServlet {
  private static final TableName TABLE = TableName.valueOf("gae-hello");

//  final static Logger logger = LoggerFactory.getLogger(HelloServlet.class);

/**
 * getAndUpdateVisit will just increment and get the visits:visits column, using
 * incrementColumnValue does the equivalent of locking the row, getting the value, incrementing
 * the value, and writing it back.  Also, unlike most other APIs, the column is assumed to have
 * a Counter data type (actually very long as it's 8 bytes)
 *
 * You will note that we get a new table with each request, this is a lightweight operation and it
 * is preferred to caching.
 **/
  public String getAndUpdateVisit(String id) throws IOException {
    long result;

    log("getAndUpdateVisit-b4 getConnection/getTable ****");
    try (Table t = BigtableHelper.getConnection().getTable( TABLE )) {
      log("getAndUpdateVisit-after table, b4 incr");
      // incrementColumnValue(row, family, column qualifier, amount)
      result = t.incrementColumnValue(Bytes.toBytes(id), Bytes.toBytes("visits"),
                                              Bytes.toBytes("visits"), 1);
      log("getAndUpdateVisit-after Incr");
    } catch (IOException e) {
      log("getAndUpdateVisit", e);
      return "0 error "+e.toString();
    }
    return String.valueOf(result);
  }

  @Override
  public void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    UserService userService = UserServiceFactory.getUserService();
    User currentUser = userService.getCurrentUser();

    if (currentUser != null) {
      resp.setContentType("text/plain");
      resp.getWriter().println("Hello, " + currentUser.getNickname());
      resp.getWriter().println("You have visited " + getAndUpdateVisit(currentUser.getUserId()));
    } else {
      resp.sendRedirect(userService.createLoginURL(req.getRequestURI()));
    }
  }
}
