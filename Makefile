
all:
	$(MAKE) -C app all

%:
	$(MAKE) -C app $@

.PHONY: all %
