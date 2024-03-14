package com.neo.twig;

public final class TimeService {
    private float timeScale;
    private boolean m_Paused;

    private float m_RunTime;
    private float m_DeltaTime;

    public TimeService() {
        timeScale = 1f;
        m_Paused = false;
        m_DeltaTime = 0;
    }

    public void updateDelta(float highResDeltaTime) {
        if (m_Paused) return;

        m_DeltaTime = highResDeltaTime * timeScale;
        m_RunTime += m_DeltaTime;
    }

    public float getDeltaTime() {
        return m_DeltaTime;
    }

    public void step() {
        m_DeltaTime += m_DeltaTime;
    }
}
