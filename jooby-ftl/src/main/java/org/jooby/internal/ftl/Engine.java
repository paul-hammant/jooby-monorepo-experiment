/**
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jooby.internal.ftl;

import static java.util.Objects.requireNonNull;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.jooby.MediaType;
import org.jooby.Renderer;
import org.jooby.View;

import freemarker.template.Configuration;
import freemarker.template.SimpleHash;
import freemarker.template.Template;
import freemarker.template.TemplateModel;

public class Engine implements View.Engine {

  private Configuration freemarker;

  private String prefix;

  private String suffix;

  public Engine(final Configuration freemarker, final String prefix, final String suffix) {
    this.freemarker = requireNonNull(freemarker, "Freemarker config is required.");
    this.prefix = prefix;
    this.suffix = suffix;
  }

  @Override
  public void render(final View view, final Renderer.Context ctx) throws Exception {
    String name = prefix + view.name() + suffix;

    Template template = freemarker.getTemplate(name, ctx.charset().name());

    Map<String, Object> hash = new HashMap<>();

    // locals
    hash.putAll(ctx.locals());

    // model
    hash.putAll(view.model());
    TemplateModel model = new SimpleHash(hash, new FtlWrapper(freemarker.getObjectWrapper()));

    // TODO: remove string writer
    StringWriter writer = new StringWriter();

    // output
    template.process(model, writer);
    ctx.type(MediaType.html)
        .send(writer.toString());
  }

  @Override
  public String name() {
    return "ftl";
  }

  @Override
  public String toString() {
    return name();
  }

}