package perf;

import ratpack.launch.*;
import ratpack.handling.*;
import ratpack.func.*;
import ratpack.server.*;
import ratpack.perf.incl.*;

public class HandlerFactory implements ratpack.launch.HandlerFactory {

  public Handler create(LaunchConfig launchConfig) throws Exception {
    return Handlers.chain(launchConfig, new ChainAction() {
      public void execute(Chain chain) {
        chain.

        handler("stop", new StopHandler()).

        handler("render", new Handler() {
          public void handle(Context context) {
            context.render("ok");
          }
        }).

        handler("direct", new Handler() {
          public void handle(Context context) {
            context.getResponse().send("ok");
          }
        });

        for (int i = 0; i < 100; ++ i) {
          chain.handler("handler" + i, new Handler() {
            public void handle(Context context) {
              throw new RuntimeException("unexpected");
            }
          });
        }

        chain.handler("manyHandlers", new Handler() {
          public void handle(Context context) {
            context.getResponse().send();
          }
        });

      }
    });
  }

}