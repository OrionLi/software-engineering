export const initPerformanceMonitoring = () => {
  // 页面加载性能
  window.addEventListener('load', () => {
    const timing = performance.getEntriesByType('navigation')[0] as PerformanceNavigationTiming;
    
    console.log('页面加载时间:', timing.loadEventEnd - timing.navigationStart);
    console.log('DOM解析时间:', timing.domComplete - timing.domInteractive);
    console.log('资源加载时间:', timing.loadEventEnd - timing.domContentLoadedEventEnd);
  });

  // 资源加载性能
  const observer = new PerformanceObserver((list) => {
    list.getEntries().forEach((entry) => {
      if (entry.entryType === 'resource' && entry.duration > 1000) {
        console.warn('资源加载过慢:', entry.name, entry.duration);
      }
    });
  });

  observer.observe({ entryTypes: ['resource'] });
}; 