
test:
	make -C app test

clean:
	make -C app clean

lint:
	make -C app lint

build:
	make -C app build

install:
	make -C app install

report:
	make -C app report

start:
	make -C app start

generate-migrations:
	make -C app generate-migrations

tart-dist:
	make -C app start-dist

build-run: build

.PHONY: build