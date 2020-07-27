create_test() {
  touch "scenarios/$1.expected"
  touch "scenarios/$1.scenario"
}

create_tests_from_name() {
  for ((i=2; i<$1; i++)) do
      create_test "$2$i"
  done
}

#create_tests_from_name 10 "mix"
