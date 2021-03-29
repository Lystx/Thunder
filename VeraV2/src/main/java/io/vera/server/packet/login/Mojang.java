package io.vera.server.packet.login;

import io.vera.server.concurrent.PoolSpec;
import io.vera.server.concurrent.ServerThreadPool;
import io.vera.server.net.NetData;
import io.vson.VsonValue;
import io.vson.elements.object.VsonObject;
import io.vson.enums.FileFormat;
import io.vson.manage.vson.VsonParser;
import io.vson.other.TempVsonOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.concurrent.ThreadSafe;
import javax.net.ssl.HttpsURLConnection;

@ThreadSafe
public final class Mojang<T> {

    private static final ServerThreadPool SCHEDULER = ServerThreadPool.forSpec(PoolSpec.SCHEDULER);
    private final HttpsURLConnection c;
    private volatile Function<VsonValue, T> callback;
    private volatile Function<String, T> exception;

    private Mojang(HttpsURLConnection connection) {
        this.c = connection;
    }

    public static <T> Mojang<T> req(String format, String... fill) {
        try {
            URL url = new URL(String.format(format, (Object[])fill));
            URLConnection connection = url.openConnection();
            return new Mojang<>((HttpsURLConnection)connection);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public Future<T> get() {
        Callable<T> get = () -> {
            try {
                this.c.setRequestMethod("GET");
                this.c.setRequestProperty("User-Agent", "Mozilla/5.0");
                this.c.setRequestProperty("Content-Type", "application/json");
                this.c.setDoOutput(true);
                this.c.setDoInput(true);
                int code = this.c.getResponseCode();
                if (code != 200)
                    return (T) this.exception.apply(String.valueOf(code));
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.c.getInputStream()))) {
                    return (T) this.callback.apply((new VsonParser(reader, new TempVsonOptions())).parse());
                }
            } catch (IOException e) {
                return (T) this.exception.apply(e.getMessage());
            }
        };
        return SCHEDULER.submit(get);
    }

    public Future<T> post(VsonObject element) {
        Callable<T> post = () -> {
            try {
                this.c.setRequestMethod("POST");
                this.c.setRequestProperty("User-Agent", "Mozilla/5.0");
                this.c.setRequestProperty("Content-Type", "application/json");
                this.c.setDoOutput(true);
                this.c.setDoInput(true);
                try (OutputStream out = this.c.getOutputStream()) {
                    out.write(element.asVsonObject().toString(FileFormat.RAW_JSON).getBytes(NetData.NET_CHARSET));
                }
                int code = this.c.getResponseCode();
                if (code != 200)
                    return (T)this.exception.apply(String.valueOf(code));
                try (BufferedReader reader = new BufferedReader(new InputStreamReader(this.c.getInputStream()))) {
                    VsonParser vsonParser = new VsonParser(reader, new TempVsonOptions());
                    return (T)this.callback.apply(vsonParser.parse());
                }
            } catch (IOException e) {
                return (T)this.exception.apply(e.getMessage());
            }
        };
        return SCHEDULER.submit(post);
    }

    public Mojang<T> callback(Consumer<VsonValue> consumer) {
        return callback(resp -> {
            consumer.accept(resp);
            return null;
        });
    }

    public Mojang<T> callback(Function<VsonValue, T> func) {
        this.callback = func;
        return this;
    }

    public Mojang<T> onException(Function<String, T> func) {
        this.exception = func;
        return this;
    }
}
