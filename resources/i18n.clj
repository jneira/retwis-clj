@tower/config
=>
{:dev-mode?      true
 :default-locale :en
 :dictionary
 {:en {:example {:foo       ":en :example/foo text"
                 :foo_note  "Hello translator, please do x"
                 :bar {:baz ":en :example.bar/baz text"}
                 :greeting  "Hello {0}, how are you?"
                 :with-markdown "<tag>**strong**</tag>"
                 :with-exclaim! "<tag>**strong**</tag>"}}
  :missing  "<Translation missing: {0}>"}
 :en-US      {:example {:foo ":en-US :example/foo text"}}
 :en-US-var1 {:example {:foo ":en-US-var1 :example/foo text"}}

 :log-missing-translation-fn!
 (fn [{:keys [dev-mode? locale k-or-ks]}] k-or-ks)}
