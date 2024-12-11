import { createApp } from 'vue';
import App from './App.vue';
import router from './router';
import 'animate.css';

// Element Plus 按需引入
import {
  ElButton,
  ElInput,
  ElForm,
  ElFormItem,
  ElCard,
  ElMessage,
  ElIcon,
  ElRadio,
  ElRadioGroup,
  ElEmpty
} from 'element-plus';
import 'element-plus/dist/index.css';

const components = [
  ElButton,
  ElInput,
  ElForm,
  ElFormItem,
  ElCard,
  ElIcon,
  ElRadio,
  ElRadioGroup,
  ElEmpty
] as const;

const plugins = [
  ElMessage
] as const;

const app = createApp(App);

components.forEach(component => {
  if (component.name) {
    app.component(component.name, component);
  }
});

plugins.forEach(plugin => {
  app.use(plugin);
});

app.use(router);
app.mount('#app'); 