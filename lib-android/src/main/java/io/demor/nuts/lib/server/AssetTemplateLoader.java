package io.demor.nuts.lib.server;

import android.content.res.AssetManager;

import com.google.common.io.CharStreams;
import com.google.common.io.Closeables;
import com.x5.template.providers.TemplateProvider;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class AssetTemplateLoader extends TemplateProvider {

    final AssetManager mAssetManager;

    public AssetTemplateLoader(final AssetManager assetManager) {
        mAssetManager = assetManager;
    }

    @Override
    public String getProtocol() {
        return "android";
    }

    @Override
    public String loadContainerDoc(final String docName) throws IOException {
        final InputStream in = mAssetManager.open("web/temp-" + docName.replaceAll(".chtml", "") + ".html");
        try {
            return CharStreams.toString(new InputStreamReader(in));
        } finally {
            Closeables.closeQuietly(in);
        }
    }
}