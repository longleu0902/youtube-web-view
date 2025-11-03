package com.example.youtubewebview;

import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private WebView youtubeView;
    private FrameLayout fullscreenContainer;
    private View customView;
    private WebChromeClient.CustomViewCallback customViewCallback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
    }

    private void initViews() {
        youtubeView = findViewById(R.id.youtube_view);
        fullscreenContainer = findViewById(R.id.fullscreen_container);

        WebSettings webSettings = youtubeView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webSettings.setDomStorageEnabled(true);
        webSettings.setDatabaseEnabled(true);
        webSettings.setMixedContentMode(WebSettings.MIXED_CONTENT_ALWAYS_ALLOW);

        youtubeView.setWebViewClient(new WebViewClient());
        youtubeView.setWebChromeClient(new MyChromeClient());
        youtubeView.loadUrl("https://www.youtube.com");
    }

    private class MyChromeClient extends WebChromeClient {
        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            // Khi YouTube bật fullscreen
            if (customView != null) {
                callback.onCustomViewHidden();
                return;
            }

            customView = view;
            fullscreenContainer.addView(view);
            fullscreenContainer.setVisibility(View.VISIBLE);
            customViewCallback = callback;
            youtubeView.setVisibility(View.GONE);

            // 🔥 Kích hoạt fullscreen toàn hệ thống khi người dùng phóng to video
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            hideSystemUI();
        }

        @Override
        public void onHideCustomView() {
            // Khi người dùng thoát fullscreen
            if (customView == null) return;

            fullscreenContainer.removeView(customView);
            fullscreenContainer.setVisibility(View.GONE);
            customView = null;
            youtubeView.setVisibility(View.VISIBLE);
            customViewCallback.onCustomViewHidden();

            // 🔙 Hiển thị lại status bar, navigation bar
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            showSystemUI();
        }
    }

    private void hideSystemUI() {
        // Immersive fullscreen (ẩn thanh trạng thái + điều hướng)
        getWindow().getDecorView().setSystemUiVisibility(
                View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                        | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_FULLSCREEN
                        | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                        | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
        );
    }

    private void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
    }

    @Override
    public void onBackPressed() {
        if (customView != null) {
            ((WebChromeClient) youtubeView.getWebChromeClient()).onHideCustomView();
        } else if (youtubeView.canGoBack()) {
            youtubeView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}
