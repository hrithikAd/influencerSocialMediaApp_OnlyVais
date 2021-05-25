module.exports = {
  root: true,
  env: {
    es6: true,
    node: true,

    // "isolatedModules": true,               /* Transpile each file as a separate module (similar to 'ts.transpileModule'). */

        "skipLibCheck": true,
        /* Strict Type-Checking Options */
        "strict": true,


  },
  extends: [
    "eslint:recommended",
    "google",
  ],
  rules: {
    quotes: ["error", "double"],
  },
};
