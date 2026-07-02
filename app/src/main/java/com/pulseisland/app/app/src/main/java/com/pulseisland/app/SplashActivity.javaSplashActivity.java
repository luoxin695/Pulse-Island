package com.pulseisland.app;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {

    private PulseIslandAPI pulseAPI;
    private Handler handler = new Handler();
    private boolean checkPassed = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 心电图动画视图
        FrameLayout layout = new FrameLayout(this);
        layout.setBackgroundColor(Color.parseColor("#0D0D0F"));
        
        ECGWaveView ecgView = new ECGWaveView(this);
        layout.addView(ecgView);
        
        setContentView(layout);

        // 初始化检测
        pulseAPI = new PulseIslandAPI(this);
        
        // 模拟检测过程（实际检测很快，这里加一点延迟让动画可见）
        handler.postDelayed(() -> {
            checkPassed = pulseAPI.runAllChecks();
            if (checkPassed) {
                // 检测通过，进入主界面（后面再实现）
                // startActivity(new Intent(SplashActivity.this, MainActivity.class));
                // finish();
            } else {
                // 检测失败，退出
                finishAffinity();
                System.exit(0);
            }
        }, 3000); // 3秒后完成检测，后续可替换为真实异步检测
    }

    // 心电图自定义View
    class ECGWaveView extends View {
        
        private Paint paint;
        private Path path;
        private float phase = 0;
        private ValueAnimator animator;
        
        public ECGWaveView(Context context) {
            super(context);
            paint = new Paint();
            paint.setColor(Color.parseColor("#4A90D9"));
            paint.setStrokeWidth(3);
            paint.setStyle(Paint.Style.STROKE);
            paint.setAntiAlias(true);
            paint.setStrokeCap(Paint.Cap.ROUND);
            paint.setStrokeJoin(Paint.Join.ROUND);
            
            path = new Path();
            
            // 心电图跳动动画
            animator = ValueAnimator.ofFloat(0, (float) (2 * Math.PI));
            animator.setDuration(2000); // 2秒一个心跳周期
            animator.setRepeatCount(ValueAnimator.INFINITE);
            animator.setInterpolator(new LinearInterpolator());
            animator.addUpdateListener(animation -> {
                phase = (float) animation.getAnimatedValue();
                invalidate();
            });
            animator.start();
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);
            
            int width = getWidth();
            int height = getHeight();
            int centerY = height / 2;
            
            path.reset();
            path.moveTo(0, centerY);
            
            float waveLength = width / 4f;
            
            for (float x = 0; x <= width; x += 2) {
                // 基础正弦波 + 心电图的尖峰脉冲
                float baseWave = (float) Math.sin((x / waveLength) * 2 * Math.PI + phase) * 20;
                
                // 在中间位置加入QRS波群（心电图的特征尖峰）
                float qrsX = width * 0.5f;
                float distToQRS = Math.abs(x - qrsX);
                float pulse = 0;
                if (distToQRS < 80) {
                    // Q波
                    if (distToQRS > 60 && distToQRS <= 80) {
                        pulse = -(distToQRS - 60) * 0.5f;
                    }
                    // R波（主峰）
                    else if (distToQRS > 40 && distToQRS <= 60) {
                        pulse = -(60 - distToQRS) * 5;
                    }
                    else if (distToQRS > 20 && distToQRS <= 40) {
                        pulse = -(distToQRS - 20) * 5;
                    }
                    // S波
                    else if (distToQRS <= 20) {
                        pulse = distToQRS * 1.5f;
                    }
                }
                
                float y = centerY + baseWave + pulse;
                path.lineTo(x, y);
            }
            
            canvas.drawPath(path, paint);
        }

        @Override
        protected void onDetachedFromWindow() {
            super.onDetachedFromWindow();
            if (animator != null) {
                animator.cancel();
            }
        }
    }
}
