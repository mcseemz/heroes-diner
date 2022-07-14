package com.mcseemz.diner.commands;

import org.springframework.shell.component.MultiItemSelector;
import org.springframework.shell.component.SingleItemSelector;
import org.springframework.shell.component.support.SelectorItem;
import org.springframework.shell.standard.AbstractShellComponent;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@ShellComponent
public class SingleSelectorTest extends AbstractShellComponent {

    @ShellMethod(key = "component multi", value = "Multi selector", group = "Components")
    public String multiSelector() {
        List<SelectorItem<String>> items = new ArrayList<>();
        items.add(SelectorItem.of("key1", "value1"));
        items.add(SelectorItem.of("key2", "value2", false));
        items.add(SelectorItem.of("key3", "value3"));
        MultiItemSelector<String, SelectorItem<String>> component = new MultiItemSelector<>(getTerminal(),
                items, "testSimple", null);
        component.setResourceLoader(getResourceLoader());
        component.setTemplateExecutor(getTemplateExecutor());
        MultiItemSelector.MultiItemSelectorContext<String, SelectorItem<String>> context = component
                .run(MultiItemSelector.MultiItemSelectorContext.empty());
        String result = context.getResultItems().stream()
                .map(si -> si.getItem())
                .collect(Collectors.joining(","));
        return "Got value " + result;
    }

}
